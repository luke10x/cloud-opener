export default function Date({ dateString }: { dateString: string }) {
  return <time dateTime={dateString}>{dateString}</time>
}