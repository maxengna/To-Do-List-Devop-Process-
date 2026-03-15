FROM node:20-alpine AS builder

WORKDIR /react-app

COPY package*.json ./
RUN npm install

COPY . .  
RUN npm run build 

#--------------------------------------- RUN ----------------------------------------

FROM node:20-alpine AS production 

WORKDIR /react-app

# Copy the entire project context from the builder stage
# This includes node_modules (with vite binaries) and the dist folder
COPY --from=builder /react-app .

EXPOSE 4173

CMD [ "npm", "run", "preview" ]