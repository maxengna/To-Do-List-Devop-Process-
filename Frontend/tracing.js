import { WebTracerProvider } from '@opentelemetry/sdk-trace-web'
import { SimpleSpanProcessor } from '@opentelemetry/sdk-trace-web'
import { OTLPTraceExporter } from '@opentelemetry/exporter-trace-otlp-http'
import { registerInstrumentations } from '@opentelemetry/instrumentation'

import { FetchInstrumentation } from '@opentelemetry/instrumentation-fetch'
import { XMLHttpRequestInstrumentation } from '@opentelemetry/instrumentation-xml-http-request'
import { DocumentLoadInstrumentation } from '@opentelemetry/instrumentation-document-load'

const provider = new WebTracerProvider()

const exporter = new OTLPTraceExporter({
  url: "http://my-opentelemetry-collector:4318/v1/traces"
})

provider.addSpanProcessor(new SimpleSpanProcessor(exporter))

provider.register()

registerInstrumentations({
  instrumentations: [
    new DocumentLoadInstrumentation(),
    new FetchInstrumentation({
      propagateTraceHeaderCorsUrls: [/http:\/\/localhost/]
    }),
    new XMLHttpRequestInstrumentation()
  ],
})