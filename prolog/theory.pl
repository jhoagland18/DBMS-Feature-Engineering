in(X,[X|_]).
in(X,[_|R]) :- in(X,R).

% the function bin_boundaries returns all X,Y bin boundaries for a given numeric attribute
bin_boundaries(X,Y,Table,Attribute) :-
	bin_thresholds(Table,Attribute,L),
	bin_boundaries(X,Y,L).
	
bin_boundaries(X,Y,[A,B|T]) :-
	(X=A,Y=B);
	bin_boundaries(X,Y,[B|T]).

%% UTILITIES

% append a list of atoms
my_atom_concat([H|T],Out) :-
	my_atom_concat(H,T,Out).
my_atom_concat(A,[B],Out) :-
	atom_concat(A,B,Out).
my_atom_concat(A,[H|T],Out) :-
	atom_concat(A,H,S1),
	my_atom_concat(S1,T,Out).

%% TIME_STAMP MANAGEMENT
time_stamp_in_both_tables(T,U):-
	attribute(T,_,timestamp,_),
	attribute(U,_,timestamp,_).

% if T0 of type T and T1 of type U have both timestamps, Out=AND T.Timestamp < U.Timestamp
time_stamp_condition(T,U,T0,T1,Out) :-
	attribute(T,A,timestamp,_),
	attribute(U,B,timestamp,_),
	my_atom_concat(['AND ',T0,'.',A,' < ',T1,'.',B],Out).

time_stamp_condition(T,U,_,_,Out) :-
	\+time_stamp_in_both_tables(T,U),
	Out=''.
	
%%% PK MANAGEMENT %%%%
% out will be t.PK0, t.PK1, ...
sql_unpackPK(Table,T,Out) :-
	pk(Table,PKs),
	sql_unpackPK(T,PKs,'',Out).

sql_unpackPK(T,[PK],CurString,Out) :-
	my_atom_concat([CurString,T,'.',PK],Out).

sql_unpackPK(T,[PK|N],CurString,Out) :-
	my_atom_concat([CurString,T,'.',PK,', '],S4),
	sql_unpackPK(T,N,S4,Out).

%%% JOIN MANAGEMENT %%%%
% out will be t1.FK1_0 = t2.FK1_0 AND t1.FK1_1 = t2.FK1_1 AND  ...
sql_ON(T1,T2,[FK1],[FK2],CurString,Out) :-
	my_atom_concat([CurString,T1,'.',FK1,' = ', T2, '.', FK2],Out).

sql_ON(T1,T2,[FK1|R1],[FK2|R2],CurString,Out) :-
	my_atom_concat([CurString,T1,'.',FK1,' = ',T2,'.',FK2,' AND '],S1),
	sql_ON(T1,T2,R1,R2,S1,Out).



pk('Purchases',['Purchase_ID']).
attribute('Purchases', 'date', 'timestamp', 'timestamp').
attribute('Purchases', 'return', 'zero_one', 'zero-one').
attribute('Purchases', 'online', 'zero_one', 'zero-one').
pk('Clients',['Client_ID']).
attribute('Clients', 'age', 'numeric', 'years').
bin_thresholds('Clients','age',[0.0,20.0,30.0,40.0,50.0,60.0,100000.0]).
attribute('Clients', 'gender', 'nominal', 'gender').
important_values('Clients','gender',['M','F']).
pk('Products',['Product_ID']).
attribute('Products', 'price', 'numeric', 'dollars').
bin_thresholds('Products','price',[0.0,20.0,100.0,200.0,1000.0,1000000.0]).
relationship('Purchases','Clients',['Client_ID'],['Client_ID'],to1).
relationship('Purchases','Products',['Product_ID'],['Product_ID'],to1).
relationship('Clients','Purchases',['Client_ID'],['Client_ID'],toN).
relationship('Products','Purchases',['Product_ID'],['Product_ID'],toN).