# Use official lightweight Node.js 20 image based on Alpine Linux
FROM node:20-alpine

# Set working directory inside container
WORKDIR /app

# Copy package files first for optimal Docker layer caching
COPY package*.json ./

# Install only production dependencies
RUN npm ci --only=production

# Copy application files
COPY server.js ./
COPY public ./public

# Cloud Run defaults to PORT 8080
ENV PORT=8080
EXPOSE 8080

# Start Dapur Rantau server
CMD ["node", "server.js"]
