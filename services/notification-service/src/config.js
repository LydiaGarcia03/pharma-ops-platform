export const config = {
  kafka: {
    brokers: (process.env.KAFKA_BOOTSTRAP_SERVERS ?? 'localhost:9092').split(','),
    groupId: 'notification-service',
    topic: process.env.KAFKA_TOPIC_INVENTORY_UPDATED ?? 'pharmaops.inventory.updated',
  },
  redis: {
    host: process.env.REDIS_HOST ?? 'localhost',
    port: parseInt(process.env.REDIS_PORT ?? '6379', 10),
  },
  notification: {
    // TTL em segundos para a chave de idempotência (padrão: 1 hora)
    idempotencyTtlSeconds: parseInt(process.env.NOTIFICATION_IDEMPOTENCY_TTL ?? '3600', 10),
  },
};
