import { gql } from '@apollo/client/core'

export const LOGIN_MUTATION = gql`
  mutation Login($input: LoginInput!) {
    login(input: $input) {
      accessToken
      passwordResetRequired
      user {
        id
        name
        email
      }
    }
  }
`

export const CREATE_PRODUCT_MUTATION = gql`
  mutation CreateProduct($input: CreateProductInput!) {
    createProduct(input: $input) {
      id
      name
      barcode
      controlled
      salePrice
      active
    }
  }
`

export const CREATE_SALE_MUTATION = gql`
  mutation CreateSale($input: CreateSaleInput!) {
    createSale(input: $input) {
      id
      status
      total
      createdAt
    }
  }
`

export const ADD_BATCH_MUTATION = gql`
  mutation AddBatch($input: AddBatchInput!) {
    addBatch(input: $input) {
      id
      productId
      batchNumber
      expirationDate
      initialQuantity
    }
  }
`

export const RESTOCK_MUTATION = gql`
  mutation Restock($input: RestockInput!) {
    restock(input: $input)
  }
`

export const PROCESS_RETURN_MUTATION = gql`
  mutation ProcessReturn($input: ProcessReturnInput!) {
    processReturn(input: $input) {
      id
      saleId
      productId
      quantity
      reason
      createdAt
    }
  }
`
