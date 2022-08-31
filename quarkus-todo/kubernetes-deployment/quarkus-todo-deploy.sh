#!/bin/bash

kubectl apply -f quarkus-todo-configmap.yaml
kubectl apply -f quarkus-todo-secret.yaml
kubectl apply -f quarkus-todo-deployment.yaml
kubectl apply -f quarkus-todo-service.yaml
minikube service quarkus-todo --url -p micbn

echo "--------------------------------------"

kubectl describe svc quarkus-todo