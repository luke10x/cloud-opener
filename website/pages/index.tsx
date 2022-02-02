import Link from 'next/link'
import { GetStaticProps } from 'next'
import Head from 'next/head'
import Layout, { siteTitle } from '../components/layout'
import utilStyles from '../styles/utils.module.css'
import { getSortedPostsData } from '../lib/posts'
import { createCloudlink, buildCloudlinkUrl, pollCloudlinkUntilExchangeFound} from '../lib/backend'
import DownloadableQRCode from '../components/qrcode'
import { useEffect, useState } from 'react'
import { Cloudlink } from '../lib/generated/typescript-fetch-client'


export default function Home({
  allPostsData
}: {
  allPostsData: {
    date: string
    title: string
    id: string
  }[]
}) {

  const [cloudlink, setCloudlink] = useState<Cloudlink>(undefined)
  useEffect(() => {
    createCloudlink().then((cloudlink) => {
      setCloudlink(cloudlink)
        console.log("starting to poll: ", cloudlink)
        pollCloudlinkUntilExchangeFound(cloudlink).then((exchange) => {
          console.log({exchange})
        }).catch(() => cloudlink)

    }).catch(() => cloudlink)
  }, [])

  return (
    <Layout home>
      <Head>
        <title>{siteTitle}</title>
      </Head>
      <section className={utilStyles.headingMd}>
        {cloudlink && <>
          <DownloadableQRCode value={cloudlink.code} />
        </>}
        {!cloudlink && <div>Spinning circle...</div>}

        <p>Every time you visit this website a cloud link is created. Scan this link with your camera enabled device and the exchange between this window and the device will be established!</p>
        <p>
          For the best experience use our app from {' '}
          <a href="https://nextjs.org/learn">Google Play store</a>
        </p>
      </section>
    </Layout>
  )
}

export const getStaticProps: GetStaticProps = async () => {
  const allPostsData = getSortedPostsData()
  return {
    props: {
      allPostsData
    }
  }
}