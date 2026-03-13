'use strict'

const { NodeSDK } = require('@opentelemetry/sdk-node')
const { getNodeAutoInstrumentations } = require('@opentelemetry/auto-instrumentations-node')
const { OTLPTraceExporter } = require('@opentelemetry/exporter-trace-otlp-http')

const sdk = new NodeSDK({
  traceExporter: new OTLPTraceExporter({
    url: "http://otel-collector:4318/v1/traces"  // otel-collector is the service name of the opentelemetry-collector in the k8s cluster
  }),
  instrumentations: [getNodeAutoInstrumentations()],
})

sdk.start()