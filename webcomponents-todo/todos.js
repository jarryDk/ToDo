import { store } from "./store.js";

class ToDosElement extends HTMLElement {

    constructor() { 
        super();
        this.root = this.attachShadow({ mode: 'open' });
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
        return await fetch('./todos.json')
            .then(response => response.json());
    }

    view() {        
        let toDoMap = store.toDoStore.toDoMap;
        for (let [key, value] of toDoMap) {
            console.log(value);
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

        this.todo = store.toDoStore.getToDo(this.id);
        console.log("todo : ", this.todo);

        this.root.appendChild(this.template());        
        
        this.view();
       
    }

    view() { 
        const title = this.root.querySelector("[data-title]");
        title.innerText = this.todo.subject;

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
                    <slot name="title" data-title>t</title>
                </header>
                <slot name="content" data>c</title>
            </article>
            `;
            ToDoElement.cachedTemplate = templateElement.content;
        }
        return ToDoElement.cachedTemplate.cloneNode(true);
    }

};
customElements.define("jarry-todo", ToDoElement);
