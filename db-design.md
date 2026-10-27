# データベース設計書

## 1. 目的

requirements.md と handout_app_spec.md をもとに、アプリが使う `todos` テーブルの項目と作成方法を定めます。テーブル（データを行と列で保存する表）は1つだけです。

## 2. テーブル一覧

| テーブル名 | 用途 | 1行が表すもの |
|---|---|---|
| `todos` | やることの保存 | 1件のやること |

## 3. `todos` テーブル定義

| カラム名 | 内容・対応する要件 | 型 | NULL（空値） | 初期値・自動設定 |
|---|---|---|---|---|
| `id` | 1件を区別する番号。編集・削除の対象指定、操作記録の対象番号に対応 | `BIGINT` | 不可 | 1から自動採番。主キー（行を一意に識別する項目） |
| `title` | やること。一覧表示と名前による部分一致検索に使用 | `VARCHAR(255)` | 不可 | なし |
| `detail` | メモ。任意入力。操作記録には内容を残さない | `VARCHAR(255)` | 可 | なし |
| `category` | ジャンル。5種類から選択し、一覧のジャンル絞り込みに使用 | `VARCHAR(255)` | 不可 | なし |
| `priority` | 優先度。`1`＝高、`2`＝中、`3`＝低 | `INT` | 不可 | `2` |
| `due_date` | 期限。任意入力。一覧の期限順並び替えに使用 | `DATE` | 可 | なし |
| `completed` | 完了状態。`1`＝完了、`0`＝未完了 | `BOOLEAN` | 不可 | `FALSE`（未完了） |
| `created_at` | 登録日時 | `DATETIME` | 不可 | MySQLが登録時に自動設定 |
| `updated_at` | 最終更新日時 | `DATETIME` | 不可 | MySQLが登録時に自動設定し、更新時に自動更新 |
| `deleted_at` | 削除した日時。値がある行は通常の一覧/API/MCPから除外 | `DATETIME` | 可 | 削除時に設定し、復元時に空にする |

### 3.1 ジャンルに保存する値

`category` には次のいずれかを保存します。

- デザイン
- マーケティング
- プログラミング
- 資格
- 就職活動

### 3.2 入力と表示に関する対応

利用者が入力する項目は `title`、`detail`、`category`、`priority`、`due_date` です。編集時には `completed` も変更します。`id`、`created_at`、`updated_at` は利用者の入力欄に出さず、自動で設定します。

画面表示では、`priority` の数値を「高／中／低」、`completed` の真偽値を「完了／未完了」に置き換えます。

### 3.3 要件とカラムの対応

| 要件 | 対応するカラム |
|---|---|
| やることの登録・編集・一覧表示 | `title` |
| メモの登録・編集 | `detail` |
| ジャンルの選択・一覧表示・ジャンル絞り込み | `category` |
| 優先度の選択・一覧表示 | `priority` |
| 期限の入力・一覧表示・期限順の並び替え | `due_date` |
| 完了状態の編集・一覧表示 | `completed` |
| 1件の識別、編集・削除対象の指定 | `id` |
| 登録日時と更新日時の自動記録 | `created_at`、`updated_at` |
| 名前による部分一致検索 | `title` を検索対象とする |

## 4. 主キー

`id` を主キー（各行に重複しない識別番号）とします。値は `AUTO_INCREMENT` によりMySQLが自動で増やします。

## 5. テーブル作成SQL（DDL）

DDL（テーブルなどの構造を作るSQL）は次のとおりです。

```sql
CREATE TABLE todos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    detail VARCHAR(255) NULL,
    category VARCHAR(255) NOT NULL,
    priority INT NOT NULL DEFAULT 2,
    due_date DATE NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,
    PRIMARY KEY (id)
);
```

## 6. 設計上の補足

- 入力文字数の上限（`title` と `detail` は255文字）および必須入力の確認は、アプリの入力チェック要件にもとづきます。
- ジャンルの5種類と優先度の数値の意味は、仕様書に定められた値です。
- 登録・編集・削除の操作記録はアプリのログに残す要件です。ログ用テーブルは設けず、`title` と `detail` の内容も記録対象にしません。
- 検索、絞り込み、並び替えは既存の `title`、`category`、`due_date` の値を使う機能であり、専用カラムは追加しません。
- 仕様書にないカラムやテーブルは追加していません。
