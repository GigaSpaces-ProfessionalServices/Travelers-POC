###########################################
# Bastion EC2 instance for WireGuard VPN
###########################################
module "bastion-sg" {
  count   = var.enable_bastion ? 1 : 0
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 4.17"

  name   = "${var.name}-bastion-sg"
  vpc_id = module.vpc.vpc_id

  egress_rules        = ["all-all"]
  ingress_cidr_blocks = length(var.whitelist_cidrs) > 0 ? var.whitelist_cidrs : ["0.0.0.0/0"]
  ingress_rules       = ["https-443-tcp", "http-80-tcp"] #"ssh-tcp"

  ingress_with_cidr_blocks = [
    { description = "WireGuardUDP"
      from_port   = 51820
      to_port     = 51820
      protocol    = "udp"
      cidr_blocks = "0.0.0.0/0"
    },
    {
      description = "WireGuardTCP"
      from_port   = 51820
      to_port     = 51820
      protocol    = "tcp"
      cidr_blocks = "0.0.0.0/0"
    },
    { description = "SSH"
      from_port   = 22
      to_port     = 22
      protocol    = "tcp"
      cidr_blocks = "0.0.0.0/0"
    },
  ]

  tags = var.tags
}

resource "aws_eip" "public" {
  count    = var.enable_bastion ? 1 : 0
  domain   = "vpc"
  tags     = merge(var.tags, { Name = "${var.name}-bastion-public-ip" })
  instance = module.bastion[0].id
}

module "bastion" {
  count   = var.enable_bastion ? 1 : 0
  source  = "terraform-aws-modules/ec2-instance/aws"
  version = "~> 4.3.1"

  create_iam_instance_profile = true

  name          = "${var.name}-bastion"
  iam_role_name = "${var.name}-bastion-iam-role"
  #   ami  = var.ami
  #   key_name = var.key_name

  subnet_id              = module.vpc.public_subnets[0]
  vpc_security_group_ids = [module.bastion-sg[0].security_group_id]
  #   associate_public_ip_address = true
  volume_tags = var.tags
  tags        = var.tags
  user_data   = var.bastion_user_data

  root_block_device = [{
    volume_size = 30
    encrypted   = true
    volume_type = "gp3"
  }]
}

resource "aws_iam_role_policy_attachment" "ssm-policy" {
  count      = length(module.bastion) > 0 ? 1 : 0
  role       = lookup(module.bastion[0], "iam_role_name", null)
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}
