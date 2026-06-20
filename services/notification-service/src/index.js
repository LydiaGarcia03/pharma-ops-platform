import { Kafka } from 'kafkajs';
import { config } from './config.js';
import { handleInventoryUpdated, closeRedis } from './notifier.js';

const kafka = new Kafka({
  clientId: 'notification-service',
  brokers: config.kafka.brokers,
});

const consumer = kafka.consumer({ groupId: config.kafka.groupId });

async function run() {
  await consumer.connect();
  console.log(`[Kafka] Connected. Subscribing to topic: ${config.kafka.topic}`);

  await consumer.subscribe({ topic: config.kafka.topic, fromBeginning: false });

  await consumer.run({
    eachMessage: async ({ topic, partition, message }) => {
      const raw = message.value?.toString();
      if (!raw) return;

      try {
        const envelope = JSON.parse(raw);
        const { eventType, correlationId, payload } = envelope;

        if (eventType !== 'InventoryUpdated') {
          console.warn(`[Consumer] Unexpected eventType: ${eventType} — skipping`);
          return;
        }

        console.log(`[Consumer] Received InventoryUpdated: correlationId=${correlationId} partition=${partition} offset=${message.offset}`);

        await handleInventoryUpdated(payload);

      } catch (err) {
        console.error(`[Consumer] Failed to process message at offset=${message.offset}:`, err.message);
      }
    },
  });
}

async function shutdown() {
  console.log('[Shutdown] Disconnecting...');
  await consumer.disconnect();
  await closeRedis();
  process.exit(0);
}

process.on('SIGTERM', shutdown);
process.on('SIGINT', shutdown);

run().catch((err) => {
  console.error('[Fatal] Failed to start notification-service:', err);
  process.exit(1);
});
