# ---- Build Stage ----
FROM node:24 AS builder
WORKDIR /app

# Copy package files first for caching
COPY package*.json ./

# Install dependencies using npm install (works across OS)
RUN npm install

# Copy the rest of the source code
COPY . .

# Build the production-ready React app
RUN npm run build

# ---- Production Stage ----
FROM nginx:alpine

# Copy built files from builder stage
COPY --from=builder /app/build /usr/share/nginx/html

# Expose Nginx port
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
