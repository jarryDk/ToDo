import ToDos from './todos.js';

class ToDoApp { 

    constructor(){
        this.todos = new ToDos();
        this.getToDos = this.getToDos.bind(this);        
        this.toDoButton = document.querySelector('#slow');
        this.init();
    }

    init() {         
        this.toDoButton.onclick = this.getToDos;
    }

    async getToDos() { 
        let result = await this.todos.todos;           
        this.output(result);
    }

    output(data) { 
        for (let row of data) { 
            console.log(row);
        } 
    }
     
}

new ToDoApp();

class ToDosElement extends HTMLElement {

    connectedCallback() { 
        console.log("connected - ToDos");
        this.innerHTML = `
            <jarry-todo data-title="Hoo">ToDo</jarry-todo>            
        `;
    }

};

class ToDoElement extends HTMLElement {

    constructor() { 
        super();
        this.root = this.attachShadow({mode: 'open'});
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

customElements.define("jarry-todos", ToDosElement);
customElements.define("jarry-todo", ToDoElement);