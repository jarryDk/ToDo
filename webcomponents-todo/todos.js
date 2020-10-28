export default class ToDos{

    get todos() { 
        return new Promise((resolve, reject) => { 
            resolve(this.fetchFromServer());
        });
        
    }


    async fetchFromServer() { 
        return await fetch('./todos.json')
            .then(response => response.json());
    }
}