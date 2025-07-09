# Commands

## Build-Push

```sh
cd /home/ubuntu/prj/Travelers/xap-kafka/example/mirror
mvn clean package
docker build -t mirror .
docker tag mirror localhost:32000/mirror:latest
docker push localhost:32000/mirror:latest
```

## Install 

```sh
helm install mirror dihrepo/xap-pu --version 17.0.1-patch-b-1 --set instances=1,partitions=0,resourceUrl=pu.jar,image.repository=localhost:32000/mirror,image.tag=latest -n dih
```

## Uninstall

```sh
helm uninstall mirror -n dih
```
