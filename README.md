# Spring Websockets Example App
A demo application that manages drawing canvases via websockets with Spring. 

[![Java CI](https://github.com/JFL110/spring-websockets-example/workflows/Java%20CI/badge.svg)](https://github.com/JFL110/spring-websockets-example/actions?query=workflow%3A%22Java+CI%22) [![Upload to ECR & ECS](https://github.com/JFL110/spring-websockets-example/workflows/Upload%20to%20ECR%20&%20ECS/badge.svg)](https://github.com/JFL110/spring-websockets-example/actions?query=workflow%3A%22Upload+to+ECR+%26+ECS%22) [![codecov](https://codecov.io/gh/JFL110/spring-websockets-example/branch/master/graph/badge.svg?token=RpMWGDu3b4)](https://codecov.io/gh/JFL110/spring-websockets-example)

- The frontend to this app is hosted [here on Cloudfront](http://d1kzdlgex69htr.cloudfront.net/random)
- Frontend source is [here](https://github.com/JFL110/spring-websockets-example-frontend)
- The status of the App can be checked [here](http://springwebsocketsexample2-env.eba-9wepzsai.eu-west-2.elasticbeanstalk.com/)

## Operation
The application uses Spring to expose a websocket that clients use to send and receive messages regarding the creation, continuation and termination of lines drawn by users on a canvas. Canvas' have an identifier determined by the user's URL. The messages are applied to a simple in-memory state representation of each canvas and forwarded to all other clients that are viewing the same canvas. Upon initialisation, clients are sent a complete set of all the lines on the canvas. A channel is also exposed to allow clients to clear the canvas.

To keep the consumed resources of this app within the AWS free tier, only a single EC2 instance is used to host this and other ECS projects. Limits are placed on the number and size of canvases and a scheduled task is used to perform cleanup. Expansion of this application would require a way to share canvas state between instances, or to cleverly route requests to specific instances.

Websocket operation is tested by spinning up an in-test webserver and performing real client interactions. Concurrent modification and cleanup of canvases is also tested.

A simple text endpoint is exposed to show the state of the application [here](http://springwebsocketsexample2-env.eba-9wepzsai.eu-west-2.elasticbeanstalk.com/).

## Infrastructure
The application is packaged as a Docker container and deployed on AWS ECS. Automatic deployment is triggered on release, using Github Actions. This includes building and testing the application with Gradle, pushing to ECR and updating the ECS service to pick-up the latest image.

The frontend is hosted in an S3 bucket and served via a Cloudfront distribution. [Lambda@Edge functions](https://github.com/JFL110/spring-websockets-example-frontend/blob/master/cloudfront-lamda-edge.md) are used to route all requests to a single page, implement a caching policy and to serve pre-compressed GZip and Brotli content where possible. These optimisations give the frontend a score of over 97% on [PageSpeed Insights](https://developers.google.com/speed/pagespeed/insights/?url=http%3A%2F%2Fd1kzdlgex69htr.cloudfront.net%2F&tab=mobile).

