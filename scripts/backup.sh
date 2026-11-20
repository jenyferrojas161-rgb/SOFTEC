#!/usr/bin/env bash
# Copia de seguridad de la base softech_db (comprimida) y limpieza de copias de más de 14 días.
# Uso:  SOFTEC_DB_PASSWORD=tu_clave ./scripts/backup.sh [carpeta_destino]
# Programarla a diario con cron, por ejemplo:  0 2 * * * cd /ruta/backend && ./scripts/backup.sh
# Restaurar:  gunzip -c backups/archivo.sql.gz | psql -h localhost -U postgres softech_db
set -euo pipefail

DESTINO="${1:-./backups}"
mkdir -p "$DESTINO"
ARCHIVO="$DESTINO/softech_db_$(date +%Y%m%d_%H%M%S).sql.gz"

PGPASSWORD="${SOFTEC_DB_PASSWORD:?Define la variable SOFTEC_DB_PASSWORD}" \
  pg_dump -h "${SOFTEC_DB_HOST:-localhost}" -p "${SOFTEC_DB_PORT:-5432}" \
          -U "${SOFTEC_DB_USER:-postgres}" softech_db | gzip > "$ARCHIVO"

find "$DESTINO" -name 'softech_db_*.sql.gz' -mtime +14 -delete
echo "Copia creada: $ARCHIVO"
