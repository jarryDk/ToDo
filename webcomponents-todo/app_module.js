import * as storage from './store.js';

class ToDosElement extends HTMLElement {

    constructor() {
        super();
        this.root = this.attachShadow({mode: 'open'});
        
        this.store = new storage.Store('todos');
        this.loadToDos = this.loadToDos.bind(this);
        this.populatePosts = this.populatePosts.bind(this);

        this.toDoUpdate = document.querySelector('#update');
        this.toDoPopuulate = document.querySelector('#populate');

        this.init();
    }

    connectedCallback() {
        console.debug("connected - ToDos");
        customElements
            .whenDefined('jarry-todo')
            .then(_ => this.populatePosts());
    }

    get todos() {
        return new Promise((resolve, reject) => {
            resolve(this.fetchFromServer());
        });
    }

    async fetchFromServer() {
        return await fetch(restServiceUrl, {
            headers: {
                'Authorization': 'Bearer ' + keycloak.token
            }
        }).then(response => response.json());
    }

    init() {
        this.store.store([]);
        this.toDoUpdate.onclick = this.loadToDos;
        this.toDoPopuulate.onclick = this.populatePosts;
    }

    loadToDos() {
        console.debug('loadToDos');

        let storeTodos = function(result){
            this.store.store(result);
        }
        storeTodos = storeTodos.bind(this);

        let requestToDos = function(){
            try {
                let todos = this.todos;
                todos.then(storeTodos);
            } catch (e) {
                console.error('Error:', e);
            }
        }
        requestToDos = requestToDos.bind(this);

        if (keycloak.authenticated) {
            keycloak.updateToken(30)
                .then(requestToDos);
        } else {
            requestToDos();
        }
    }

    populatePosts() {

        console.debug('populatePosts');

        let result = this.store.load();
        const todo = this.querySelector("jarry-todo");

        if( Array.isArray(result) ){
            for (let row of result) {
                console.debug("row",row);
                const clonedTodo = todo.cloneNode(true);
                clonedTodo.todo = row;
                let theFirstChild = this.root.firstChild;
                if (theFirstChild != null){
                    this.root.insertBefore(clonedTodo, theFirstChild);
                } else {
                    this.root.appendChild(clonedTodo);
                }
            }
        } else{
            console.error("todos in localStorage was not an array!", result);
        }
    }

};

class ToDoElement extends HTMLElement {

    constructor() {
        super();
        this.root = this.attachShadow({mode: 'open'});
        this._todo = {};
    }

    get todo(){
        return this._todo;
    }

    set todo(todo){
        this._todo = todo;
    }

    connectedCallback() {
        console.debug("connected - ToDo");

        this.root.appendChild(this.template());
        const subject = this.root.querySelector("[data-subject]");
        subject.innerText = this.todo.subject;
        const body = this.root.querySelector("[data-body]");
        body.innerText = this.todo.body;
    }

    template() {
        if (!ToDoElement.cachedTemplate) {
            const templateElement = document.createElement("template");
            templateElement.innerHTML = `
            <article>
                <h2 data-subject>t</h2>
                <p data-body>c</p>
            </article>
            `;
            ToDoElement.cachedTemplate = templateElement.content;
        }
        return ToDoElement.cachedTemplate.cloneNode(true);
    }

};

customElements.define("jarry-todos", ToDosElement);
customElements.define("jarry-todo", ToDoElement);