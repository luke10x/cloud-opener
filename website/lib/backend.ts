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

export const pollCloudlinkUntilExchangeHandleIsPresent = (cloudlink: Cloudlink): Promise<string> => {

  const g = (resolve: (exchangeHandle: string) => void, reject: (error: Error) => void) => {
    const f = async () => {
      console.log("trying to fetch cloudlink")
      cloudlink = await getApi().getCloudlink(cloudlink).catch((e: Error) => {
        console.log("Creation of the cloudlink failed", e)
        throw new Error("Failure to fetch a Cloudlink to see if it has an Exchange handle")
      });
  
      if (cloudlink.exchangeHandle) {
        clearInterval(timerId); 
        console.log("Cloudlink has an exchange handle")
        resolve(cloudlink.exchangeHandle)
      }
    }
  
    let timerId = setInterval(f, 2000);
  }

  const exchangeHandlePromise: Promise<string> = new Promise(g)
  return exchangeHandlePromise;
}

export const buildCloudlinkUrl = (cloudlink: Cloudlink): string => `${config.BACKEND_BASE_URL}/cloudlink/${cloudlink.code}`

export const pollExchange = async (exchangeHandle: string, onRefresh: (exchange: Exchange) => void) => {
  const f = async () => {
    const exchange = await getApi().getExchange({ handle: exchangeHandle}).catch((e: Error) => {
      console.log("🐖 Fetching Exchange", e)
      throw e
    });
    onRefresh(exchange);
  }

  let timerId = setInterval(f, 2000);
}