#!/bin/bash

# Docker Hub credentials
DOCKER_USER=ahmedhesham890
DOCKER_TOKEN=dckr_pat_LOfKgcY48EjaLQJ0EPWLyKx4TSc

# Login to Docker Hub

echo "$DOCKER_TOKEN" | docker login -u "$DOCKER_USER" --password-stdin

# Build and push UserApp
cd UserApp
./mvnw clean package -DskipTests
docker build -t $DOCKER_USER/user-app:latest .
docker push $DOCKER_USER/user-app:latest
cd ..

# Build and push PostApp
cd PostApp
./mvnw clean package -DskipTests
docker build -t $DOCKER_USER/post-app:latest .
docker push $DOCKER_USER/post-app:latest
cd ..

# Build and push NotficationApp
cd NotficationApp
./mvnw clean package -DskipTests
docker build -t $DOCKER_USER/notification-app:latest .
docker push $DOCKER_USER/notification-app:latest
cd ..

# Build and push ModeratorApp
cd ModeratorApp
./mvnw clean package -DskipTests
docker build -t $DOCKER_USER/moderator-app:latest .
docker push $DOCKER_USER/moderator-app:latest
cd ..

echo "All images built and pushed successfully!"

# Apply Kubernetes manifests
kubectl apply -f k8s/ --recursive

echo "All Pods created successfully!"
