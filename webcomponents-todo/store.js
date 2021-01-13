// https://github.com/AdamBien/webstandards.training/blob/master/js-playground/src/store.js

export class ToDoStore { 

    constructor() {     
        this.toDoMap = new Map();   
    }

    loadToDos(todos){  
        console.info("todos",todos);
        for (let row of todos) {
            this.toDoMap.set(row.id + "", row);
        }       
    }

    toDoMapSorted(){
        return this.toDoMap;
    }

    create(id, toDo){       
        return this.toDoMap.set(id + "" ,toDo);
    }

    read(id){       
        return this.toDoMap.get(id + "");
    }

    update(id, toDo){       
        return this.toDoMap.set(id + "" ,toDo);
    }

    delete(id){       
        return this.toDoMap.delete(id + "");
    }

    

}

const store = {};
store.toDoStore = new ToDoStore();

export { store };

export class Store { 

    constructor(name) { 
        this.slot = name;
        this.storage = window.localStorage;
    }

    store(response) { 
        const stringified = JSON.stringify(response);
        this.storage.setItem(this.slot,stringified);
    }
 
    load() { 
        const stringified = this.storage.getItem(this.slot);
        return JSON.parse(stringified);
    }

}

