import { ApolloClient, InMemoryCache, createHttpLink, from } from '@apollo/client/core'
import { setContext } from '@apollo/client/link/context'
import { store } from '@/app/store'

function authLink() {
  return setContext((_, { headers }) => {
    const token = store.getState().auth.token
    return {
      headers: {
        ...headers,
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
    }
  })
}

function makeClient(uri: string) {
  return new ApolloClient({
    link: from([authLink(), createHttpLink({ uri })]),
    cache: new InMemoryCache(),
  })
}

export const identityClient = makeClient('/api/identity/graphql')
export const inventoryClient = makeClient('/api/inventory/graphql')
export const salesClient = makeClient('/api/sales/graphql')
