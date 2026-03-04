#Setup con storage 
#*********************************************
#*********************************************

#https://docs.azure.cn/en-us/container-apps/storage-mounts-azure-files?tabs=powershell


az login --tenant d37a9e56-6856-40e7-9d5a-7e562d273e52

#One time only 
az provider register --namespace Microsoft.OperationalInsights
az provider register --namespace Microsoft.App
az provider register -n Microsoft.Storage --wait


$RESOURCE_GROUP="c2-group"
$ENVIRONMENT_NAME="c2-dev"
$LOCATION="spaincentral"
$STORAGE_ACCOUNT_NAME="conductumc2storagedev"
$STORAGE_SHARE_NAME="c2uploads"
$STORAGE_MOUNT_NAME="c2devstoragemount"
$CONTAINER_APP_NAME="c2-dev-app" 


az group create --name $RESOURCE_GROUP --location $LOCATION --query "properties.provisioningState"

az containerapp env create --name $ENVIRONMENT_NAME --resource-group $RESOURCE_GROUP --location "$LOCATION" --query "properties.provisioningState"

az storage account create --resource-group $RESOURCE_GROUP --name $STORAGE_ACCOUNT_NAME --location "$LOCATION" --kind StorageV2 --sku Standard_LRS --enable-large-file-share --query provisioningState

az storage share-rm create --resource-group $RESOURCE_GROUP --storage-account $STORAGE_ACCOUNT_NAME --name $STORAGE_SHARE_NAME --quota 1024 --enabled-protocols SMB --output table

$STORAGE_ACCOUNT_KEY=$(az storage account keys list -n $STORAGE_ACCOUNT_NAME --query "[0].value" -o tsv)

az containerapp env storage set --access-mode ReadWrite --azure-file-account-name $STORAGE_ACCOUNT_NAME --azure-file-account-key $STORAGE_ACCOUNT_KEY --azure-file-share-name $STORAGE_SHARE_NAME --storage-name $STORAGE_MOUNT_NAME --name $ENVIRONMENT_NAME --resource-group $RESOURCE_GROUP --output table

az containerapp create --name $CONTAINER_APP_NAME --resource-group $RESOURCE_GROUP --environment $ENVIRONMENT_NAME --image nginx --min-replicas 1 --max-replicas 1 --target-port 80 --ingress external --query properties.configuration.ingress.fqdn

az containerapp show --name $CONTAINER_APP_NAME --resource-group $RESOURCE_GROUP --output yaml > app.yaml

#Edit yaml 

az containerapp update --name $CONTAINER_APP_NAME --resource-group $RESOURCE_GROUP --yaml app.yaml --output table



#delete 
az group delete --name $RESOURCE_GROUP

