import { GetStaticPaths, GetStaticProps } from 'next'
import Head from 'next/head'
import Layout, { siteTitle } from '../../components/layout'
import utilStyles from '../../styles/utils.module.css'
import { useEffect, useState } from 'react'
import { Exchange } from '../../lib/generated/typescript-fetch-client'
import { useRouter } from 'next/router'
import { ParsedUrlQuery } from 'querystring'
import { pollExchange } from '../../lib/backend'

export default function OneExchange() {

  const router = useRouter()
  const exchangeHandle = router.query.handle

  const [exchange, setExchange] = useState<Exchange>(undefined)
  useEffect(() => {
    pollExchange(exchangeHandle as string, (polledExchange) => {
      setExchange(polledExchange)
    })
  }, [])
  return (
    <Layout home>
      <Head>
        <title>{siteTitle}</title>
      </Head>
      <section className={utilStyles.headingMd}>
        handle={exchangeHandle}
        {exchange && <>{JSON.stringify(exchange)}</>}
        {!exchange && <div>Spinning circle...</div>}
        <p>
          exchange
        </p>
      </section>
    </Layout>
  )
}

export const getStaticProps: GetStaticProps = async () => {
  return {
    props: {}
  }
}

export const getStaticPaths: GetStaticPaths<ParsedUrlQuery> = async () => {
  const arr: string[] = []
  const paths = arr.map((handle) => {
      return {
          params: { handle },
      }
  })
  return { paths, fallback: 'blocking' } 
}
