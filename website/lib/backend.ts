import { DefaultApi, Configuration, Cloudlink, Exchange } from "./generated/typescript-fetch-client"
import { config } from "./config"

const getApiFactory = (): (() => DefaultApi) =>  {

  var api: DefaultApi | undefined = undefined

  return (): DefaultApi => {
    if (api) {
      return api;
    }
    api = new DefaultApi(new Configuration({basePath: config.BACKEND_BASE_URL}))
    return api
  }
}

const getApi = () => getApiFactory()()

export const createCloudlink = async (): Promise<Cloudlink> => {
  return await getApi().createCloudlink().catch((e: Error) => {
    console.log("Creation of the cloudlink failed")
    throw new Error("Failure to create a Cloudlink")
  });
}

export const pollCloudlinkUntilExchangeFound = (cloudlink: Cloudlink): Promise<Exchange> => {


  const g = (resolve: (exchange: Exchange) => void, reject: (error: Error) => void) => {
    const f = async () => {
      console.log("trying to fetch cloudlink")
      cloudlink = await getApi().getCloudlink(cloudlink).catch((e: Error) => {
        console.log("Creation of the cloudlink failed", e)
        throw new Error("Failure to fetch a Cloudling to see if it has an Exchange handle")
      });
  
      if (cloudlink.exchangeHandle) {
        clearInterval(timerId); 
        console.log("timer stopped")
  
  
        const exchange = await getApi().getExchange({ handle: cloudlink.exchangeHandle}).catch((e: Error) => {
          console.log("Creation of the cloudlink failed")
          throw new Error("Failure trying to fetch Exchange")
        });

        resolve(exchange)
  
      }
    }
  
    let timerId = setInterval(f, 2000);
  }

  const exchangePromise: Promise<Exchange> = new Promise(g)
  return exchangePromise;
}

export const buildCloudlinkUrl = (cloudlink: Cloudlink): string => `${config.BACKEND_BASE_URL}/cloudlink/${cloudlink.code}`
