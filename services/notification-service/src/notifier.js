import Redis from 'ioredis';
import { config } from './config.js';

const redis = new Redis({ host: config.redis.host, port: config.redis.port });

redis.on('error', (err) => console.error('[Redis] Connection error:', err.message));

/**
 * Processa um evento InventoryUpdated e decide se deve notificar.
 * Retorna true se a notificação foi disparada, false se foi ignorada (idempotência).
 */
export async function handleInventoryUpdated(payload) {
  const { productId, storeId, currentQuantity, minimumQuantity, movementType } = payload;

  const isStockDepleted  = currentQuantity === 0;
  const isBelowMinimum   = currentQuantity > 0 && currentQuantity < minimumQuantity;

  if (!isStockDepleted && !isBelowMinimum) {
    return false;
  }

  const notifType = isStockDepleted ? 'STOCK_DEPLETED' : 'STOCK_LOW';
  const idempotencyKey = `notif:${notifType.toLowerCase()}:${productId}:${storeId}`;

  // Verifica se já notificamos recentemente (idempotência via Redis)
  const alreadyNotified = await redis.get(idempotencyKey);
  if (alreadyNotified) {
    console.log(`[Notifier] Skipped duplicate ${notifType}: productId=${productId} storeId=${storeId}`);
    return false;
  }

  // Registra a notificação com TTL
  await redis.set(idempotencyKey, '1', 'EX', config.notification.idempotencyTtlSeconds);

  // channel: LOG — email/push são Fase 9
  const message = isStockDepleted
    ? `ALERT [${notifType}] Product ${productId} at store ${storeId} is OUT OF STOCK. Movement: ${movementType}`
    : `ALERT [${notifType}] Product ${productId} at store ${storeId} is below minimum (current=${currentQuantity}, min=${minimumQuantity}). Movement: ${movementType}`;

  console.log(`[Notifier] ${message}`);
  return true;
}

export async function closeRedis() {
  await redis.quit();
}
