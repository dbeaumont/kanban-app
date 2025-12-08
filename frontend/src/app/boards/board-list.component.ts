import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { BoardApiService, Board } from './board.service';

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule],
  selector: 'app-board-list',
  template: `
    <section>
      <h2>Boards</h2>
      <div class="board-grid">
        <article *ngFor="let board of boards" (click)="open(board)" class="card">
          <h3>{{board.name}}</h3>
          <p>{{board.description}}</p>
        </article>
      </div>
    </section>
  `,
  styles: [`
    .board-grid { display:grid; grid-template-columns: repeat(auto-fill, minmax(240px,1fr)); gap:12px; }
    .card { border:1px solid #e2e8f0; border-radius:10px; padding:12px; cursor:pointer; background:#fff; transition:box-shadow .2s; }
    .card:hover { box-shadow:0 6px 20px rgba(0,0,0,.08); }
  `]
})
export class BoardListComponent implements OnInit {
  boards: Board[] = [];
  constructor(private api: BoardApiService, private router: Router) {}

  ngOnInit(): void {
    this.api.list().subscribe(b => this.boards = b);
  }

  open(board: Board) {
    this.router.navigate(['/boards', board.id]);
  }
}
