# GraphQL server

```sh
❯ curl -s 'http://localhost:8088/api/graphql' --data-binary '{"query":"query{\n employees(role: SoftwareDeveloper){\n name\n role\n}\n}"}' | jq .
{
  "data": {
    "employees": [
      {
        "name": "Maria",
        "role": "SoftwareDeveloper"
      },
      {
        "name": "Peter",
        "role": "SoftwareDeveloper"
      }
    ]
  }
}
```
