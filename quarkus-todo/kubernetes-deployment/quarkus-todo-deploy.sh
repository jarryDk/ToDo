#!/bin/bash

kubectl apply -f quarkus-todo-configmap.yaml
kubectl apply -f quarkus-todo-secret.yaml
kubectl apply -f quarkus-todo-deployment.yaml
kubectl apply -f quarkus-todo-service-loadbalancer.yaml
# kubectl apply -f quarkus-todo-service-nodeport.yaml

echo "--------------------------------------"
kubectl describe svc quarkus-todo
echo "--------------------------------------"

minikube service quarkus-todo --url -p micbn

