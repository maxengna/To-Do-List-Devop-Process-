FROM node:20-alpine AS builder

WORKDIR /react-app

COPY package*.json ./
RUN npm install

COPY . .  
RUN npm run build 

#--------------------------------------- RUN ----------------------------------------

FROM node:20-alpine AS production 

WORKDIR /react-app/prod

# copy Machine code files from build stage to run stage 
COPY --from=builder /react-app/dist ./dist

COPY vite.config.js package.json ./
RUN npm install --production
  
EXPOSE 4173

CMD [ "npm", "run", "preview" ]