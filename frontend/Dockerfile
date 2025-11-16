# Use Node 24 for building and running React app
FROM node:24

WORKDIR /app

# Copy package files and install dependencies
COPY package.json package-lock.json ./
RUN npm install

# Copy source code
COPY . .

# Expose dev server port
EXPOSE 3000

# Start React development server
CMD ["npm", "start"]
