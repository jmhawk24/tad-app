terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "~> 4.16"
    }
  }

  required_version = ">= 1.2.0"
}

provider "aws" {
  region = "us-west-2"
}

resource "aws_vpc" "tad_app_vpc" {
  cidr_block = "10.0.0.0/16"

  tags = {
    Name = "vpc for tadapp ec2 and rds"
  }
}

resource "aws_subnet" "public_subnet" {
  vpc_id = aws_vpc.tad_app_vpc.id
  cidr_block = "10.0.1.0/24"
  availability_zone = "1a"
}

resource "aws_subnet" "private_subnet" {
  vpc_id = aws_vpc.tad_app_vpc.id
  cidr_block = "10.0.2.0/24"
  availability_zone = "1a"
}

resource "aws_internet_gateway" "ig" {
  vpc_id = aws_vpc.tad_app_vpc.id
}

resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.tad_app_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.ig.id
  }

  route {
    ipv6_cidr_block = "::/0"
    gateway_id = aws_internet_gateway.ig.id
  }
}

resource "aws_route_table_association" "public_1_rt_a" {
  subnet_id = aws_subnet.public_subnet.id
  route_table_id = aws_route_table.public_rt.id
}

resource "aws_key_pair" "generated_key" {
  key_name = "tadapp_ssh.pem"
  public_key = var.public_ssh_key
}

resource "aws_security_group" "jacob_basic" {
  name = "jacob_basic"
  description = "HTTP, HTTPS, SSH"
  vpc_id = aws_vpc.tad_app_vpc.id
}

resource "aws_security_group_rule" "egress" {
  type = "egress"
  from_port = 0
  to_port = 0
  protocol = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
  ipv6_cidr_blocks = ["::/0"]
  security_group_id = aws_security_group.jacob_basic.id
}

resource "aws_security_group_rule" "http_ingress" {
  type = "ingress"
  from_port = 80
  to_port = 80
  protocol = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
  security_group_id = aws_security_group.jacob_basic.id
}

resource "aws_security_group_rule" "ssh_ingress" {
  type = "ingress"
  from_port = 22
  to_port = 22
  protocol = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
  security_group_id = aws_security_group.jacob_basic.id
}

resource "aws_security_group_rule" "https_ingress" {
  type = "ingress"
  from_port = 443
  to_port = 443
  protocol = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
  security_group_id = aws_security_group.jacob_basic.id
}

resource "aws_security_group_rule" "https_ingress_again" {
  type = "ingress"
  from_port = 8080
  to_port = 8080
  protocol = "tcp"
  cidr_blocks = ["0.0.0.0/0"]
  security_group_id = aws_security_group.jacob_basic.id
}

data "aws_ami" "ubuntu" {
  most_recent = true

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["099720109477"] # Canonical
}

resource "aws_instance" "app_server" {
  ami = data.aws_ami.ubuntu.id
  instance_type = "t2.micro"
  key_name = aws_key_pair.generated_key.key_name
  security_groups = [aws_security_group.jacob_basic.name]

  user_data = <<-EOF
  #!/bin/bash -ex

  amazon-linux-extras install nginx -y
  echo "<h1>$(curl https://api.kanye.rest/?format=text)</h1>" >  /usr/share/nginx/html/index.html
  systemctl enable nginx
  systemctl start nginx
  EOF

  tags = {
    Name = "server with Kanye quotes set up to nginx homepage"
  }
}
