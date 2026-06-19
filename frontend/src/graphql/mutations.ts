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
