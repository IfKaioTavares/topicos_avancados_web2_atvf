# syntax=docker/dockerfile:1

FROM node:20-alpine
WORKDIR /app

ENV NODE_ENV=production

COPY package.json package-lock.json ./
RUN npm install --omit=dev && npm cache clean --force

COPY src ./src

EXPOSE 3000

CMD ["node", "src/app.js"]
