# CloudNet-MongoDB

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)

---

## CloudNet

https://github.com/CloudNetService/CloudNet-v3

## Requirements

- [MongoDB Database](https://www.mongodb.com)
- [CloudNetv3 3.4.0-SNAPSHOT](https://github.com/CloudNetService/CloudNet-v3)

## Support

Discord: CodedInfinity | Flo#3842

## Installation

- Stop Cloudnet
- Download or Paste the module into the modules directory
- Start the Cloud
- Edit the Config and change the database in the local/registry file to "mongodb"
- Restart the Cloud

Then you're done and CloudNet uses Mongo Databases

## Docker

If you use Docker you can choose the following environment variable instead of config

    - CLOUDNET_MONGODB_HOS
    - CLOUDNET_MONGODB_PORT
    - CLOUDNET_MONGODB_USER
    - CLOUDNET_MONGODB_PASSWORD
    - CLOUDNET_MONGODB_DATABASE
    - CLOUDNET_MONGODB_AUTHMECHANISM
    - CLOUDNET_MONGODB_AUTHDB
