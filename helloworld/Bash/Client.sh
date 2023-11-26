#!/bin/bash

# Dirección y puerto del servidor
SERVIDOR=10.147.19.63
PUERTO=9099

# Número entero grande a factorizar
NUMERO=1234567890

# Función para enviar una solicitud al servidor y medir el tiempo
enviar_solicitud() {
  local to_print=100
  local start_time=$(date +%s%N)
  local response=$(echo "$to_print" | nc -w 5 $SERVIDOR $PUERTO)  # Establece un timeout de 5 segundos
  local end_time=$(date +%s%N)
  local elapsed_time=$((end_time - start_time))
  echo "Solicitud: $to_print"
  echo "Respuesta: $response"
}

# Número de clientes concurrentes
NUM_CLIENTES=100

# Enviar solicitudes al servidor en paralelo
for ((i = 1; i <= NUM_CLIENTES; i++)); do
  enviar_solicitud "$NUMERO" &
done

# Esperar a que todos los clientes terminen
wait
