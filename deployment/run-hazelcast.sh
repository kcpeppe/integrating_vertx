#!/bin/bash
set -euo pipefail

docker run --rm \
  --name hazelcast \
  --network dkrnet \
  -e 'DNS_DOMAIN=cluster.local' \
  quay.io/pires/hazelcast-kubernetes:3.9.3
