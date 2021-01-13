import { store } from "./store.js";

class ToDosElement extends HTMLElement {

    constructor() { 
        super();
        this.root = this.attachShadow({ mode: 'open' });
       
        this.socket = new WebSocket('ws://localhost:8080/ws/todos');
        this.socket.onopen = e => console.log(e);
        
        this.toDoOnWebSocket = ({ data: event }) => {
            console.log(event);
            const eventJson = JSON.parse(event);
            console.log("eventJson", eventJson);
            const id = eventJson.id;
            const todo = eventJson.toDo;
            if(eventJson.action == "CREATE"){
                store.toDoStore.create(id, todo);
            } else if(eventJson.action == "READ"){
                // No action
            } else if(eventJson.action == "UPDATE"){
                store.toDoStore.update(id, todo);
            } else if(eventJson.action == "DELETE"){
                store.toDoStore.delete(id);
            } else {
                console.warn("Ups");
            }
            this.view();
        };

        this.socket.onmessage = this.toDoOnWebSocket;

    }

    connectedCallback() {
        console.log("connected - ToDos");
        this.getToDos();        
    }

    async getToDos() {
        let result = await this.todos;       
        store.toDoStore.loadToDos(result);
        console.log("Store", store);
        this.view();
    }

    get todos() {
        return new Promise((resolve, reject) => {
            resolve(this.fetchFromServer());
        });
    }

    async fetchFromServer() {
        const uri = "http://localhost:8080/todos";
        // const uri = "./todos.json";
        return await fetch(uri)
            .then(response => response.json());
    }

    view() {        
        let toDoMap = store.toDoStore.toDoMap;        
        for (let [key, value] of toDoMap) {
            this.root.appendChild(this.todoElement(key));
        }        
    }

    todoElement(id){
        const template = document.createElement('template');
        template.innerHTML = `
        <jarry-todo id="${id}"></jarry-todo>            
        `;        
        return template.content.cloneNode("true");
    }

};
customElements.define("jarry-todos", ToDosElement);

class ToDoElement extends HTMLElement {

    constructor() {
        super();
        this.root = this.attachShadow({ mode: 'open' });
    }

    connectedCallback() {
        console.log("connected - ToDo");

        this.todo = store.toDoStore.read(this.id);
        console.log("todo : ", this.todo);

        this.root.appendChild(this.template());        
        
        this.view();
       
    }

    view() { 
        const title = this.root.querySelector("[data-title]");
        title.innerText = this.todo.subject;

        const id = this.root.querySelector("[data-id]");
        id.innerText = this.todo.id;

        const content = this.root.querySelector("[data]");
        if(this.todo.body !== undefined){                   
            content.innerText = this.todo.body;
        } else{
            content.innerText ="...";
        }
    }
    
    template() {
        if (!ToDoElement.cachedTemplate) {
            const templateElement = document.createElement("template");
            templateElement.innerHTML = `
            <style>
            :host{
                contain: content;
                display:block;
                border: 2px solid lightblue;
                padding: 0.5em;
                margin: 1em;
            }
            </style>
            <article>
                <header>
                    <slot name="title" data-title>t</slot> <slot name="id" data-id>i</slot>
                </header>
                <slot name="content" data>c</slot>
            </article>
            `;
            ToDoElement.cachedTemplate = templateElement.content;
        }
        return ToDoElement.cachedTemplate.cloneNode(true);
    }

};
customElements.define("jarry-todo", ToDoElement);
