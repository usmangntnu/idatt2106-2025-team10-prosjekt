import kartverketClient from './kartverketClient'
import type { AddressSuggestion } from '@/types/types'

export const autocompleteAddress = async (
  query: string,
  limit = 10
): Promise<AddressSuggestion[]> => {
  const resp = await kartverketClient.get<{
    adresser: Array<{
      id: string
      adressetekst: string
      representasjonspunkt: { lat: number; lon: number }
      postnummer: string
      poststed: string
    }>
  }>('/sok', {
    params: {
      sok: query,
      treffPerSide: limit,
      fuzzy: true,
    },
  })

  return resp.data.adresser.map((a) => ({
    id: a.id,
    text: a.adressetekst,
    latitude: a.representasjonspunkt.lat,
    longitude: a.representasjonspunkt.lon,
    postalCode: a.postnummer,
    city: a.poststed,
  }))
}
