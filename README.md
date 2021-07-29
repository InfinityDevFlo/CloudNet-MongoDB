# CloudNet-MongoDB

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)
[![Discord](https://img.shields.io/discord/785956343407181824.svg)](https://discord.gg/wvcX92VyEH)
[![Build Status](https://ci.vironlab.eu/job/Vextension/badge/icon)](https://ci.vironlab.eu/job/CloudNet-MongoDB/)

---

## CloudNet

https://github.com/CloudNetService/CloudNet-v3

## Requirements

- [MongoDB Database](https://www.mongodb.com)
- [CloudNetv3 3.4.0-SNAPSHOT](https://github.com/CloudNetService/CloudNet-v3)

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

<div align="center">
    <h1 style="color:#154444">Other Links:</h1>
    <a style="color:#00ff00" target="_blank" href="https://github.com/VironLab"><img src="https://img.shields.io/github/followers/VironLab?label=GitHub%20Followers&logo=GitHub&logoColor=%23ffffff&style=flat-square"></img></a>
    <a style="color:#00ff00" target="_blank" href="https://discord.gg/wvcX92VyEH"><img src="https://img.shields.io/discord/785956343407181824?label=vironlab.eu%20Discord&logo=Discord&logoColor=%23ffffff&style=flat-square"></img></a>
    <a style="color:#00ff00" target="_blank" href="https://www.paypal.com/paypalme/depascaldc"><img src="https://img.shields.io/static/v1?label=Donate%20Via%20Paypal&message=paypal&style=flat-square&logo=paypal&color=lightgrey"></img></a>
</div>