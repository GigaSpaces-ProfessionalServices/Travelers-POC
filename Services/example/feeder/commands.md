# Commands

## Build-Push

```sh
cd /home/ubuntu/prj/xap-kafka/example/feeder
mvn clean package
docker build -t feeder .
docker tag feeder localhost:32000/feeder:latest
docker push localhost:32000/feeder:latest
```

## Install 

```sh
helm install feeder dihrepo/xap-pu --version 17.1.1 --set instances=1,partitions=0,resourceUrl=pu.jar,image.repository=localhost:32000/feeder,image.tag=latest -n dih
```

## Uninstall

```sh
helm uninstall feeder -n dih
```
