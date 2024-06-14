import com.microsoft.azure.storage.CloudStorageAccount

def includes = null // ['sys-master-impex']
def excludes = ['migration'] // ['sys-master-root']

def blobClient = CloudStorageAccount.parse(configurationService.configuration.getString('media.globalSettings.cloudAzureBlobStorageStrategy.connection')).createCloudBlobClient()

blobClient.listContainers().each { c ->
  if (!includes || c.name in includes) {
    if (!(c.name in excludes)) {
      println "Remove container: ${c.name}"

      c.delete()
    }
  }
}

''