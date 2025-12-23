import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BoardApiService, Board, ColumnFilterPipe } from './board.service';
import { TaskApiService, Task } from '../tasks/task.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, ColumnFilterPipe],
  selector: 'app-board-detail',
  template: `
    <section *ngIf="board">
      <div class="board-header">
        <div>
          <h2>{{board.name}}</h2>
          <p>{{board.description}}</p>
        </div>
        <button class="primary" (click)="openCreate()">+ Créer</button>
      </div>
      <div class="columns">
        <div class="column" *ngFor="let col of columns">
          <header>{{col.name}}</header>
          <div class="task" *ngFor="let task of tasks | async | columnFilter:col.id">
            <ng-container *ngIf="isEditing(task); else viewMode">
              <label class="input-block">
                <span>Titre</span>
                <input [(ngModel)]="editTitle" />
              </label>
              <label class="input-block">
                <span>Description</span>
                <textarea [(ngModel)]="editDescription"></textarea>
              </label>
              <div class="task-actions">
                <button (click)="save(task)">Enregistrer</button>
                <button class="ghost" (click)="cancelEdit()">Annuler</button>
              </div>
            </ng-container>
            <ng-template #viewMode>
              <h4>{{task.title}}</h4>
              <p>{{task.description}}</p>
              <div class="task-actions">
                <button class="icon" (click)="startEdit(task)">✏️</button>
                <button class="icon danger" (click)="remove(task)">🗑️</button>
                <button (click)="move(task, nextColumn(col.position))">Move</button>
              </div>
            </ng-template>
          </div>
        </div>
      </div>

      <div class="modal-backdrop" *ngIf="createOpen">
        <div class="modal">
          <h3>Créer un ticket</h3>
          <label class="input-block">
            <span>Titre</span>
            <input [(ngModel)]="newTitle" name="newTitle" />
          </label>
          <label class="input-block">
            <span>Description</span>
            <textarea [(ngModel)]="newDescription" name="newDescription"></textarea>
          </label>
          <div class="task-actions">
            <button class="primary" (click)="create()">Créer</button>
            <button class="ghost" (click)="closeCreate()">Annuler</button>
          </div>
        </div>
      </div>
    </section>
  `,
  styles: [`
    .board-header { display:flex; justify-content:space-between; align-items:flex-start; gap:12px; }
    .columns { display:flex; gap:12px; overflow-x:auto; }
    .column { min-width:250px; background:#f8fafc; border:1px solid #e2e8f0; border-radius:10px; padding:10px; }
    .task { background:#fff; margin:8px 0; padding:10px; border-radius:8px; border:1px solid #e2e8f0; }
    .input-block { display:flex; flex-direction:column; gap:4px; margin-bottom:8px; font-size:13px; }
    .input-block input, .input-block textarea { border:1px solid #cbd5e1; border-radius:6px; padding:6px 8px; font: inherit; }
    .input-block textarea { min-height:70px; resize: vertical; }
    .task-actions { display:flex; gap:8px; align-items:center; margin-top:8px; }
    .task-actions .icon { background:transparent; border:1px solid #cbd5e1; padding:4px 6px; border-radius:6px; cursor:pointer; }
    .task-actions .icon.danger { border-color:#ef4444; color:#b91c1c; }
    .task-actions .ghost { background:transparent; border:1px solid #cbd5e1; padding:4px 10px; border-radius:6px; cursor:pointer; }
    .primary { background:#0f172a; color:#fff; border:1px solid #0f172a; padding:8px 12px; border-radius:6px; cursor:pointer; }
    .modal-backdrop { position:fixed; inset:0; background:rgba(0,0,0,0.45); display:flex; align-items:center; justify-content:center; }
    .modal { background:#fff; border-radius:10px; padding:16px; width: min(480px, 90vw); box-shadow:0 8px 30px rgba(0,0,0,0.12); }
  `]
})
export class BoardDetailComponent implements OnInit {
  board?: Board;
  columns = [
    { id: 'todo', name: 'To do', position: 1 },
    { id: 'doing', name: 'Doing', position: 2 },
    { id: 'done', name: 'Done', position: 3 }
  ];
  tasks = this.taskApi.list();

  constructor(private route: ActivatedRoute, private api: BoardApiService, private taskApi: TaskApiService) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.api.get(id).subscribe(b => this.board = b);
  }

  nextColumn(position: number) {
    const next = this.columns.find(c => c.position === position + 1);
    return next?.id ?? 'done';
  }

  isEditing(task: Task) {
    return this.editingTaskId === task.id;
  }

  startEdit(task: Task) {
    this.editingTaskId = task.id;
    this.editTitle = task.title;
    this.editDescription = task.description;
  }

  cancelEdit() {
    this.editingTaskId = null;
    this.editTitle = '';
    this.editDescription = '';
  }

  move(task: Task, columnId: string) {
    this.taskApi.move(task.id, columnId).subscribe(() => {
      this.tasks = this.taskApi.list();
    });
  }

  save(task: Task) {
    this.taskApi.update({ ...task, title: this.editTitle.trim(), description: this.editDescription.trim() }).subscribe(() => {
      this.tasks = this.taskApi.list();
      this.cancelEdit();
    });
  }

  create() {
    if (!this.board) return;
    if (!this.newTitle.trim()) return;
    const columnId = this.columns[0]?.id ?? 'todo';
    this.taskApi.create({
      boardId: this.board.id,
      columnId,
      title: this.newTitle.trim(),
      description: this.newDescription.trim(),
      status: 'open',
      assigneeId: '',
      labels: ''
    }).subscribe(() => {
      this.tasks = this.taskApi.list();
      this.newTitle = '';
      this.newDescription = '';
      this.closeCreate();
    });
  }

  remove(task: Task) {
    this.taskApi.delete(task.id).subscribe(() => {
      this.tasks = this.taskApi.list();
    });
  }

  openCreate() { this.createOpen = true; }
  closeCreate() { this.createOpen = false; }

  private editingTaskId: string | null = null;
  editTitle = '';
  editDescription = '';
  newTitle = '';
  newDescription = '';
  createOpen = false;
}
