# Terraform Module: Gigaspaces Smart DIH Platform as a Service (PAAS) on AWS

[![Terraform Version](https://img.shields.io/badge/Terraform-%3E%3D1.6.5-blue.svg)](https://www.terraform.io)

<br/>

## Overview

Installs Smart DIH on AWS

## Usage

This module handles Gigaspaces Smart DIH Platform running on AWS Kubernetes Engine cluster creation and configuration with Node Pools, IP MASQ, Network Policy, etc.
    It creates new VPC/EKS/S3 and installs all the required components

```hcl
module "dih" {
  source                         = "github.com/gigaspaces/terraform-aws-dih-paas"
  version                        = "~> 1.0"

  name                           = "production"
  cidr_block                     = "10.100.0.0/16"
  azs_count                      =  3

  dih_helm_version               =  16.5.0
  k8s_whitelist_cidrs            = ["0.0.0.0/0"]
  whitelist_cidrs                = ["0.0.0.0/0"]

  cluster_endpoint_public_access = true
  enable_bastion                 = false
  dih_helm_value_files           = ["dih-prod-values.yaml"]
  dih_license                    = "Tryme"

  enable_dih_default_space       = true
  enable_dih_oracle              = false
  enable_s3_buckets              = true

  enable_public_ingress          = true
  enable_internal_ingress        = false
}
