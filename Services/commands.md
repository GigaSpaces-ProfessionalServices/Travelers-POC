# Commands

```sh
export VERSION=v9
```

## Uninstall

```sh
helm uninstall feeder -n dih
helm uninstall mirror -n dih
helm uninstall processor -n dih
```


## Build-Push

```sh
cd /home/ubuntu/prj/Travelers/xap-kafka
mvn clean install
```

```sh
cd /home/ubuntu/prj/Travelers/xap-kafka/example/processor
mvn clean package
docker build -t processor .
docker tag processor localhost:32000/processor:$VERSION
docker push localhost:32000/processor:$VERSION
```

```sh
cd /home/ubuntu/prj/Travelers/xap-kafka/example/mirror
mvn clean package
docker build -t mirror .
docker tag mirror localhost:32000/mirror:$VERSION
docker push localhost:32000/mirror:$VERSION
```

```sh
cd /home/ubuntu/prj/Travelers/xap-kafka/example/feeder
mvn clean package
docker build -t feeder .
docker tag feeder localhost:32000/feeder:$VERSION
docker push localhost:32000/feeder:$VERSION
```


## Install 

```sh
helm install processor dihrepo/xap-pu --version 17.1.1 --set instances=0,partitions=1,ha=true,resourceUrl=pu.jar,image.repository=localhost:32000/processor,image.tag=$VERSION -n dih

helm install mirror dihrepo/xap-pu --version 17.1.1 --set instances=1,partitions=0,resourceUrl=pu.jar,image.repository=localhost:32000/mirror,image.tag=$VERSION -n dih

helm install feeder dihrepo/xap-pu --version 17.1.1 --set instances=1,partitions=0,resourceUrl=pu.jar,image.repository=localhost:32000/feeder,image.tag=$VERSION -n dih
```

