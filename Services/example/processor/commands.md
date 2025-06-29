# Commands

## Build-Push

```sh
cd /home/ubuntu/prj/Travelers/xap-kafka/example/processor
mvn clean package
docker build -t processor .
docker tag processor localhost:32000/processor:latest
docker push localhost:32000/processor:latest
```

## Install 

```sh
helm install processor dihrepo/xap-pu --version 17.1.1 --set instances=0,partitions=1,ha=true,resourceUrl=pu.jar,image.repository=localhost:32000/processor,image.tag=latest -n dih
```

## Uninstall

```sh
helm uninstall processor -n dih
```
