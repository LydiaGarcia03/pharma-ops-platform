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

export const STORES_QUERY = gql`
  query Stores {
    stores {
      id
      name
      active
    }
  }
`

export const BATCHES_QUERY = gql`
  query Batches {
    batches {
      id
      productId
      batchNumber
      expirationDate
      initialQuantity
    }
  }
`

export const GET_SALE_QUERY = gql`
  query GetSale($id: ID!) {
    sale(id: $id) {
      id
      status
      total
      createdAt
      items {
        id
        productId
        batchId
        quantity
        unitPrice
      }
    }
  }
`
