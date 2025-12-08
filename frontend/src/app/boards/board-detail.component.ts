import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { BoardApiService, Board, ColumnFilterPipe } from './board.service';
import { TaskApiService, Task } from '../tasks/task.service';

@Component({
  standalone: true,
  imports: [CommonModule, ColumnFilterPipe],
  selector: 'app-board-detail',
  template: `
    <section *ngIf="board">
      <h2>{{board.name}}</h2>
      <p>{{board.description}}</p>
      <div class="columns">
        <div class="column" *ngFor="let col of columns">
          <header>{{col.name}}</header>
          <div class="task" *ngFor="let task of tasks | async | columnFilter:col.id">
            <h4>{{task.title}}</h4>
            <p>{{task.description}}</p>
            <button (click)="move(task, nextColumn(col.position))">Move</button>
          </div>
        </div>
      </div>
    </section>
  `,
  styles: [`
    .columns { display:flex; gap:12px; overflow-x:auto; }
    .column { min-width:250px; background:#f8fafc; border:1px solid #e2e8f0; border-radius:10px; padding:10px; }
    .task { background:#fff; margin:8px 0; padding:10px; border-radius:8px; border:1px solid #e2e8f0; }
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

  move(task: Task, columnId: string) {
    this.taskApi.move(task.id, columnId).subscribe();
  }
}
