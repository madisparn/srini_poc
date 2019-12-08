import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {RouterModule, Routes} from "@angular/router";

import {ClientsListComponent} from './clients-list.component';
import {ClientEditComponent} from './client-edit.component';
import {AppComponent} from './app-component';
import {FormsModule} from '@angular/forms';

const routes: Routes = [
  { path: '', redirectTo: '/list', pathMatch: 'full' },
  {path: 'create', component: ClientEditComponent},
  {path: 'edit/:id', component: ClientEditComponent},
  {path: 'list', component: ClientsListComponent}
];

@NgModule({
  declarations: [
    AppComponent,
    ClientsListComponent,
    ClientEditComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    HttpClientModule,
    FormsModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
