import "./store.js";
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
        store.todos = result;
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
        console.info("store.todos", store.todos);
        for (let row of store.todos) { 
            console.log(row);

            this.root.appendChild(this.todo());
        } 

        this.innerHTML = `
            <jarry-todo data-title="Hoo">ToDo</jarry-todo>            
        `;
    }

    todo(){
        if (!ToDosElement.template) {
        const temp = document.createElement('template');
        temp.innerHTML = `
        <jarry-todo data-title="Hoo">TITLE</jarry-todo>            
        `;
        ToDosElement.template = temp;    
        }    
        return ToDosElement.template.content.cloneNode("true");
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

        this.root.appendChild(this.template());
        console.log(this);
        const title = this.root.querySelector("[data-title]");
        title.innerText = "Hej";
    }

    header() {
        const template = document.createElement('template');
        template.innerHTML = `
        <slot name="header">B</slot>
        `;
        return template.content.cloneNode(true);
    }

    footer() {
        const template = document.createElement('template');
        template.innerHTML = `
        <slot name="footer">E</slot>
        `;
        return template.content.cloneNode(true);
    }

    template() {
        if (!ToDoElement.cachedTemplate) {
            const templateElement = document.createElement("template");
            templateElement.innerHTML = `
            <article>
                <header>
                    <slot name="title" data-title>t</title>
                </header>
                <slot name="content">c</title>
            </article>
            `;
            ToDoElement.cachedTemplate = templateElement.content;
        }
        return ToDoElement.cachedTemplate.cloneNode(true);
    }

};
customElements.define("jarry-todo", ToDoElement);
