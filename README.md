# Similar-Products challenge

Hey! Welcome to the **similar-products** repository!

This repository contains a Java Spring project + Gradle that provides an API for retrieving similar products.
The API offers a single endpoint to fetch similar-products by product id.

## Endpoint

### Get Price Data

- Endpoint: `/product/{productId}/similar`
- Method: GET
- Parameters:
    - `productId`: ID of the product.
- Response:
    - Status Code: 200 (OK)
    - Body: JSON array containing similar products details objects with the following properties:
        - `id`: ID of the product.
        - `name`: name of the product.
        - `price`: price of the product.
        - `availability`: availability of the product.

## Setup and Usage

To set up and use the **prices** API, follow these steps:

1. Clone the repository: `git clone https://github.com/orsonduque/similar-products.git`
2. Navigate to the project directory: `cd similar-products`
3. Start the server: `gradle`

## Example

GET /product/2/similar
