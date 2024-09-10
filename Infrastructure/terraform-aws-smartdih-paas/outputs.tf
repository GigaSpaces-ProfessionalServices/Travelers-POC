output "vpc" {
  value = module.vpc
}

output "s3_buckets" {
  value = var.enable_s3_buckets ? {
    control = module.s3_control[0].s3_bucket_id
    data    = module.s3_data[0].s3_bucket_id
  } : null
}

output "vpn_endpoint" {
  value = try(format("https://%s", aws_eip.public[0].public_ip), null)
}

output "opensearch_dashboard_endpont" {
  value = try(aws_opensearchserverless_collection.collection[0].dashboard_endpoint, null)
}

# output "vpc" {
#   value = module.vpc
# }

# output "eks" {
#   value = module.k8s
# }

output "spacedeck" {
  value = {
    url      = local.ingress_host
    username = var.dih_admin_username
    password = coalesce(random_password.admin_password[0].result, var.dih_admin_password)
  }
  sensitive = true
}

output "grafana" {
  value = {
    url      = "https://${local.grafana_ingress_host}"
    user     = var.grafana_admin
    password = local.grafana_password
  }

  sensitive = true
}


output "s3_iam_credentials" {
  value = try({
    AWS_ACCESS_KEY_ID     = base64encode(module.s3-iam-user[0].iam_access_key_id)
    AWS_SECRET_ACCESS_KEY = base64encode(module.s3-iam-user[0].iam_access_key_secret)
    AWS_REGION            = data.aws_region.current.name
  }, null)
  sensitive = true
}
