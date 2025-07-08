export VERSION=0.6
export SPACE_VERSION=0.7
export CONSUMER_VERSION=0.13

DEPLOYMENT_FILE="./Travelers-POC/Services/example/consumer-postgres/deployment.yaml"
NAMESPACE="dih"

helm install space dihrepo/xap-pu --version 17.0.1-patch-b-1 --set instances=0,partitions=1,ha=true,resourceUrl=pu.jar,image.repository=public.ecr.aws/dih-ppc64le/travelers/space,image.tag=$SPACE_VERSION -n "$NAMESPACE"
helm install mirror dihrepo/xap-pu --version 17.0.1-patch-b-1 --set instances=1,partitions=0,resourceUrl=pu.jar,image.repository=public.ecr.aws/dih-ppc64le/travelers/mirror,image.tag=$VERSION -n "$NAMESPACE"
kubectl apply -f $DEPLOYMENT_FILE
