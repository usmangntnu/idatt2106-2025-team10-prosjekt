import axios from 'axios'

export default axios.create({
  baseURL: 'https://ws.geonorge.no/adresser/v1',
  timeout: 5_000,
})
