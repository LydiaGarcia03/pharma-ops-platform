import { gql } from '@apollo/client/core'

export const PRODUCTS_QUERY = gql`
  query Products {
    products {
      id
      name
      barcode
      controlled
      salePrice
      active
    }
  }
`
